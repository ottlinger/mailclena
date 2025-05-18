#!/bin/bash
echo "Generating new mvn site ..."
./mvnw -B clean test-compile org.pitest:pitest-maven:mutationCoverage site:site 
echo "DONE - ready to commit and push"
cp -rf target/site/* docs
git add -f docs/
git commit -a -m "Issue #7: Update site"
git push
