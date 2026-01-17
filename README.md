# physiotrack-osgi

OSGi CLI-only implementation scaffold for PhysioTrack.

## What you get
- Maven multi-module repo
- For each module: `<module>-api` and `<module>-impl` bundles
- `physiotrack-cli` bundle that starts and prints detected services
- `progress-tracking`, `personal-info`, `user-management` etc included

## Build
mvn -q -DskipTests=false clean test

## Notes
- This repo scaffolds bundles + DS components. Running them requires an OSGi framework (Felix/Equinox) + DS runtime.
- If you already have a Hello World runtime working, you can drop these built bundles into that runtime and start them.
