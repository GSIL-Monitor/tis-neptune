mvn deploy:deploy-file ^
 -DgroupId=com.dfire.tis ^
 -DartifactId=tis-solr-core ^
 -Dversion=6.0.0-fix3 ^
 -Dpackaging=jar ^
 -DpomFile=./solr-core-6.0.0.pom ^
 -Dfile=./solr-core-6.0.0.jar ^
 -DrepositoryId=releases ^
 -Durl=http://nexus.2dfire-dev.com/content/repositories/releases/ 