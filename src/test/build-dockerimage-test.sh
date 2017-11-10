if [ ! -f "Dockerfile-test-plugin" ]; then
    echo "This command must be run from the src/test directory"
    exit 1
fi

HPI="../../target/microcks-jenkins-plugin.hpi"

if [ ! -f "$HPI" ]; then
    echo "Unable to find HPI artifact (have you run mvn yet?): $HPI"
    exit 1
fi

mkdir -p jpi

cp -f "$HPI" jpi/microcks-jenkins-plugin.jpi
if [ "$?" != "0" ]; then
    echo "Unable to copy $HPI"
    exit 1
fi

docker build -f ./Dockerfile-test-plugin -t microcks/microcks-jenkins-plugin-test:latest .
if [ "$?" != "0" ]; then
    echo "Error building Jenkins snapshot image for plugin testing"
    exit 1
fi

rm -rf jpi

echo "Success ! Run docker run -p 8080:8080 -p 50000:50000 microcks/microcks-jenkins-plugin-test:latest"