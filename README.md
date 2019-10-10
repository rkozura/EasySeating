# EasySeating

## A seating chart app.

EasySeating is a cross-platform application that uses Libgdx game framework: [LIBGDX](https://libgdx.badlogicgames.com/)

Libgdx was chosen to ensure proper performance on mobile devices as well as cross-platform features.

The following features are supported:
1. Create, save, rename and delete Venues
1. Add, delete and move round tables
1. Add and remove people to the venue, as well as to tables
1. Manage people at tables
1. Import your devices list of Contacts (android is the only supported device)
1. Export your seating arrangement and list of people as a PDF

Project was created using Libgdx start guide: [Start Guide](https://libgdx.badlogicgames.com/documentation/gettingstarted/Creating%20Projects.html)

Application code is found in the core directory with the following:
- **EasySeatingGame.java** The entry point to the application
- **Assets.java** Asset manager
- **controller** Handles user input (taps)
- **logic** App state
- **object** Model
- **renderer** Rendering logic using Libgdx's OpenGL wrapper
- **resolver** Contains interfaces that device specific code must implement
- **screen** The different screens of the app (main screen, dialogs etc.)
- **tweenutil** Accessor classes used for moving ui elements using tweening logic
- **ui** Custom UI components
