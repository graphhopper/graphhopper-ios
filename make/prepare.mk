class.list: dependencies/class-exclude.grep
	@if [ "$(MAKECMDGOALS)" != "class.list" ]; then echo "error: You need to run \`make class.list\` first"; exit 1; fi
	@rm -f $@
	find graphhopper/core/src/main/java graphhopper/tools-lgpl/target/classes dependencies/hppc dependencies/jackson-annotations dependencies/jts -name '*.java' | grep -vf dependencies/class-exclude.grep >> $@
	find dependencies/fake_slf4j/src -name '*.java' >> $@

# j2objc

j2objc:
	@echo "warning: 'j2objc' doesn't exist and needs to be downloaded, \
this may take some time... To skip this, manually place the j2objc dist directory at /graphhopper-ios/j2objc."
	@curl -L -o j2objc-2.0.zip "https://github.com/google/j2objc/releases/download/2.0/j2objc-2.0.zip"; \
	unzip j2objc-2.0.zip; \
	mv j2objc-2.0 j2objc; \
	rm j2objc-2.0.zip

# JTS

dependencies/jts/jts-stripped.jar : dependencies/jts/jts-class.list dependencies/jts/jts-1.13.jar dependencies/jts/jts-1.13-sources.jar
	cd dependencies/jts/ && tar xf jts-1.13-sources.jar
	java -jar dependencies/autojar-2.1/autojar.jar -o $@ -vc dependencies/jts/jts-1.13.jar @$<
	cd dependencies/jts && rm -f jts-class-sources.list && jar tf jts-stripped.jar | while read f; do echo "$${f%.*}.java" >> jts-class-sources.list; done
	cd dependencies/jts && find com -type f -print | while read f; do grep -q "$$f" jts-class-sources.list || rm -f "$$f"; done
	cd dependencies/jts && find . -type d -empty -delete

# Trove

# find out dependencies from class list via autojar tool
dependencies/trove/trove4j-stripped.jar : dependencies/trove/trove-class.list | dependencies/trove/target/classes
	@rm -f dependencies/trove/trove4j-stripped.jar
	java -jar dependencies/autojar-2.1/autojar.jar -o $@ -c dependencies/trove/target/classes @$<

# package trove if it wasn't packaged already
dependencies/trove/target/classes:
	cd dependencies/trove && mvn package
