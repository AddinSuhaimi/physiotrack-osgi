# physiotrack-osgi

OSGi CLI-only implementation scaffold for PhysioTrack.

## What you get
- Maven multi-module repo
- For each module: `<module>-api` and `<module>-impl` bundles
- `physiotrack-cli` bundle that starts and prints detected services
- `progress-tracking`, `personal-info`, `user-management` etc included

## HOW TO BUILD AND RUN AFTER CHANGES
at repo root (../physiotrack-osgi), run:
mvn clean package

after build success navigate to ../physiotrack-osgi/runtime/felix/felix-framework-7.0.5, run:
java -jar bin/felix.jar

if you have made changes to app-runner Activator.java. run the following commands in gogo:
stop 23
update 23
start 23

## Notes
- This repo scaffolds bundles + DS components. Running them requires an OSGi framework (Felix/Equinox) + DS runtime.
- If you already have a Hello World runtime working, you can drop these built bundles into that runtime and start them.
