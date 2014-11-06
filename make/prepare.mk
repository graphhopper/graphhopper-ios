class.list: dependencies/trove/trove4j-stripped.jar dependencies/class-exclude.grep graphhopper/core/.graphhopper-compat
	@if [ "$(MAKECMDGOALS)" != "class.list" ]; then echo "error: You need to run \`make class.list\` first"; exit 1; fi
	@rm -f $@
	find graphhopper/core/src/main/java -name '*.java' | grep -vf dependencies/class-exclude.grep >> $@
	unzip -v $< | grep class | grep -v ".*$$.*" | sed s/class/java/ | sed 's/\(.*\)\(gnu\)/dependencies\/trove\/src\/gnu/' >> $@
	find dependencies/fake_slf4j/src -name '*.java' >> $@

# GraphHopper

graphhopper/core:
	@git submodule init
	@git submodule update

graphhopper/core/.graphhopper-compat: dependencies/graphhopper-compat | graphhopper/core
	@cp -R dependencies/graphhopper-compat/ graphhopper/core
	@touch $@

# j2objc

j2objc:
	@echo "warning: 'j2objc' doesn't exist and needs to be downloaded. \
This may take some time, to skip this manually place the j2objc dist directory here."
	@curl -L -o j2objc.tgz "https://www.dropbox.com/s/ffob0e7lciwoxby/j2objc.tgz?dl=0"; \
	tar xzf j2objc.tgz; \
	rm -f j2objc.tgz

# Trove

# find out dependencies from class list via autojar tool
dependencies/trove/trove4j-stripped.jar : dependencies/trove/trove-class.list | dependencies/trove/target/classes
	@rm -f dependencies/trove/trove4j-stripped.jar
	java -jar dependencies/autojar-2.1/autojar.jar -o $@ -c dependencies/trove/target/classes @$<

# package trove if it wasn't packaged already
dependencies/trove/target/classes:
	cd dependencies/trove && mvn package
