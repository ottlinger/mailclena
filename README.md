<!-- markdownlint-disable-file MD013 -->
# mailclena

Tool that logs into mail accounts and removes all mails.

## Integrations

[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/ottlinger/mailclena.svg)](http://isitmaintained.com/project/ottlinger/mailclena "Average time to resolve an issue")

[![Percentage of issues still open](http://isitmaintained.com/badge/open/ottlinger/mailclena.svg)](http://isitmaintained.com/project/ottlinger/mailclena "Percentage of issues still open")

[![Known Vulnerabilities](https://snyk.io/test/github/ottlinger/mailclena/badge.svg)](https://snyk.io/test/github/ottlinger/mailclena)

[![GPL v3.0](https://img.shields.io/github/license/ottlinger/mailclena.svg)](https://www.gnu.org/licenses/gpl-3.0.en.html)

## GitHub integrations

### GitHub Action / CI

[![GitHub Action master branch status](https://github.com/ottlinger/mailclena/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/ottlinger/mailclena/actions)

Automatic generation of webpage:

[![pages-build-deployment](https://github.com/ottlinger/mailclena/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/ottlinger/mailclena/actions/workflows/pages/pages-build-deployment)

[![CI-Site](https://github.com/ottlinger/mailclena/actions/workflows/site.yml/badge.svg)](https://github.com/ottlinger/mailclena/actions/workflows/site.yml)

### Code coverage

[![codecov](https://codecov.io/gh/ottlinger/mailclena/branch/master/graph/badge.svg)](https://codecov.io/gh/ottlinger/mailclena)

### CodeQL - quality measures

[![CodeQL](https://github.com/ottlinger/mailclena/actions/workflows/codeql.yml/badge.svg?branch=master&event=push)](https://github.com/ottlinger/mailclena/actions/workflows/codeql.yml)

### Codacy - code quality and static analysis

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/658661b0051e42518a91298797e372bd)](https://app.codacy.com/gh/ottlinger/mailclena/dashboard)

## How to run and use the application

You need to check out the application and build it with the help of [Java](https://java.sun.com) and [Maven](https://maven.apache.org/).

```bash
$ ./mvnw
Project is being built.
$ java -jar target/mailclena-0.0.1-SNAPSHOT-executable.jar
22:58:33.294 [main] INFO  de.aikiit.mailclena.MailClena - Hello World :-)
```

Init the Maven wrapper via:

```bash
mvn wrapper:wrapper
```

### Command line parameters

```bash
-usage: MailClena
- -c,--command <arg>    Command to execute - example: list or clean, default is list.
- -h,--host <arg>       Hostname - example: https://imap.yourisp.org
- -p,--password <arg>   Password - example: myfancypassword
- -u,--username <arg>   Username - example: myuser@tld.org
```

### Example call

#### Operation: clean

```bash
$ java -jar target/mailclena-0.0.1-SNAPSHOT-executable.jar -h=host.tld.org -u=yourmail@yourtld.org -p=yourpassword -c=clean
00:04:20.144 [main] INFO  de.aikiit.mailclena.MailClena - MailClena is launching with the given configuration ....
00:04:20.972 [main] INFO  de.aikiit.mailclena.mail.MailClient - Found 0 messages.
00:04:21.422 [main] INFO  de.aikiit.mailclena.mail.MailClient - Starting to delete 0 messages.
00:04:21.455 [main] INFO  de.aikiit.mailclena.mail.MailClient - Expunge folder to actually remove messages.
00:04:21.455 [main] INFO  de.aikiit.mailclena.mail.MailClient - Finished to delete 0 messages.
00:04:21.866 [main] INFO  de.aikiit.mailclena.mail.MailClient - Found 0 messages.
00:04:21.898 [main] INFO  de.aikiit.mailclena.MailClena - MailClena is shutting down .... bye bye :-)
```

#### no operation / defaults to list

```bash
$ java -jar target/mailclena-0.0.1-SNAPSHOT-executable.jar -h=host.tld.org -u=yourmail@yourtld.org -p=yourpassword
17:19:09.802 [main] INFO  de.aikiit.mailclena.MailClena - MailClena is launching with the given configuration ....
17:19:11.410 [main] INFO  de.aikiit.mailclena.mail.MailClient - No messages found - nothing to be done here.
17:19:11.410 [main] INFO  de.aikiit.mailclena.MailClena - MailClena is shutting down .... bye bye :-)
```

## Project webpage

A Maven-generated site report is also available [here](https://ottlinger.github.io/mailclena/).
