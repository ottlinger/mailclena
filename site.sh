#!/bin/bash
echo "Generating new mvn site ..."
./mvnw clean site:site 
echo "DONE - ready to commit and push"
cp -rf target/site/* docs
git add docs/
git commit -a -m "Issue #7: Update site"
git push
