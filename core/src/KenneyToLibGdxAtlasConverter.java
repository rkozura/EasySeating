import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>Created on 17.12.2014</p>
 *
 * @author NooBxGockeL
 */
public class KenneyToLibGdxAtlasConverter implements Runnable {

    private final FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            FileVisitResult fileVisitResult = super.visitFile(file, attrs);
            String fileName = file.getFileName().toString();
            if (fileName.endsWith(".xml")) {
                convertKennyXML(file);
            }
            return fileVisitResult;
        }
    };

    private static final String NEW_LINE = "\n";
    private static final int MAX_DEPTH = 10;
    private final boolean stripEndings;
    private final Path rootPath;
    private final Unmarshaller unmarshaller;

    public KenneyToLibGdxAtlasConverter(boolean stripEndings, Path rootPath) throws JAXBException {
        this.stripEndings = stripEndings;
        this.rootPath = rootPath;
        this.unmarshaller = JAXBContext.newInstance(TextureAtlas.class).createUnmarshaller();
    }

    @Override
    public void run() {
        try {
            System.out.println("=== CONVERSION PROCESS STARTED ===");
            Files.walkFileTree(rootPath, createSetOf(FileVisitOption.FOLLOW_LINKS), MAX_DEPTH, fileVisitor);
            System.out.println("=== CONVERSION PROCESS FINISHED ===");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("=== CONVERSION PROCESS ABORTED ===");
        }
    }

    private void convertKennyXML(Path file) throws IOException {
        System.out.println("converting [" + file.toString() + "]");

        InputStream inputStream = Files.newInputStream(file, StandardOpenOption.READ);

        TextureAtlas atlas = JAXB.unmarshal(inputStream, TextureAtlas.class);

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%1$s\n" +
                        "format: RGBA8888\n" +
                        "filter: Linear,Linear\n" +
                        "repeat: none\n",
                atlas.imagePath));

        for (SubTexture subTexture : atlas.subTextureList) {
            String subTextureName = subTexture.name;
            if (stripEndings) {
                subTextureName = subTextureName.substring(0, subTextureName.lastIndexOf('.'));
            }
            sb.append(String.format("%1$s\n" +
                            "  rotate: false\n" +
                            "  xy: %2$s, %3$s\n" +
                            "  size: %4$s, %5$s\n" +
                            "  orig: %4$s, %5$s\n" +
                            "  offset: 0, 0\n" +
                            "  index: -1\n",
                    subTextureName, subTexture.x, subTexture.y, subTexture.width, subTexture.height));

        }

        String fileName = file.getFileName().toString();
        Path newPath = file.getParent().resolve(fileName.substring(0, fileName.length() - 4) + ".atlas");
        Files.write(newPath, sb.toString().getBytes(), StandardOpenOption.CREATE_NEW);

        System.out.println("done [" + newPath.toString() + "]");
    }


    private static <E> Set<E> createSetOf(E... elements) {
        return new TreeSet<E>(Arrays.asList(elements));
    }

    private static void printUsage() {
        System.out.println("ONLY ONE PARAMETER: Path to directory in which to search for");
    }

    public static void main(String[] args) {
        // Uncomment next line for running inside of eclipse / IDE
        args = new String[] { "C:\\Users\\Rob\\Downloads\\uipack_fixed\\Spritesheet\\blueSheet.xml" };
        if (args.length != 1) {
            printUsage();
        }
        try {
            KenneyToLibGdxAtlasConverter converter = new KenneyToLibGdxAtlasConverter(
                    true, Paths.get(args[0]));
            converter.run();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static public class SubTexture {

        @XmlAttribute
        public String name;

        @XmlAttribute
        public String x;

        @XmlAttribute
        public String y;

        @XmlAttribute
        public String width;

        @XmlAttribute
        public String height;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static public class TextureAtlas {

        @XmlAttribute
        public String imagePath;

        @XmlElement(name = "SubTexture")
        public List<SubTexture> subTextureList;

    }
}