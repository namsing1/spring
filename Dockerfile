
# 1st stage, build the app
FROM helidon/jdk11-graalvm-maven:20.2.0 as build

WORKDIR /helidon

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
RUN  apt-get update \
  && apt-get install -y wget \
  && rm -rf /var/lib/apt/lists/*

RUN curl -k "https://repo.maven.apache.org/maven2/com/oracle/database/jdbc/ojdbc-bom/19.8.0.0/ojdbc-bom-19.8.0.0.pom"
RUN mvn package -Pnative-image -Dnative.image.skip -Dmaven.test.skip -Declipselink.weave.skip

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -Pnative-image -Dnative.image.buildStatic -DskipTests

RUN echo "done!"

# 2nd stage, build the runtime image
FROM scratch
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/helidon-quickstart-se .

ENTRYPOINT ["./helidon-quickstart-se"]

EXPOSE 9080
