varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_Position;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

uniform vec2 center;
uniform float radius;

void main() {
        vec3 color = texture2D(u_texture, v_texCoords).rgb;

        float square_dist = pow((center.x - v_Position.x),2.0) + pow((center.y - v_Position.y),2.0);
    	if (square_dist <= pow(radius,2.0)) {
    		gl_FragColor = vec4(color, 1.0);
    	} else {
    		gl_FragColor = vec4(color, 0.0);
    	}
}