#!/bin/bash
echo "Generating new mvn site ..."
# ./mvnw -B clean test-compile org.pitest:pitest-maven:mutationCoverage site:site 
./mvnw -B clean install org.pitest:pitest-maven:mutationCoverage site:site 
echo "DONE - ready to commit and push"
cp -rf target/site/* docs
git add -f docs/
git commit -a -m "Issue #7: Update site"
# DEVHINT: This may fail if any changes/commits were run in the background on the repo, e.g. dependabot/GHA automation scripts
git push
