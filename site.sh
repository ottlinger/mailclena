#!/bin/bash
echo "Generating new mvn site ..."
mvn clean site:site 
echo "DONE - ready to commit and push"
git checkout gh-pages && ./copy-site.sh 
