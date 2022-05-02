# Developer's Guide for MulTEx

In this developer's guide we describe,
with which tools and how the software is maintained.

## 1. Tools Used

### 1.1 Programming Tools

The build process is managed by Maven 3 and runs on Java 8.

A complete product and site build followed by deploys is done as follows,
if you have the correct Java and Maven on the path:

```Shell
mvn clean deploy site site:deploy
```

### 1.2 Documentation Tools

The two textual documents
(technical paper [Konzepte.pdf](../src/site/resources/Konzepte.pdf), and slides [Vortragsfolien.pdf](../src/site/resources/Vortragsfolien.pdf))
are still in german language, and edited in [LibreOffice](https://www.libreoffice.org/).

They must be transformed to PDF manually,
and then stored manually to the directory [src/site/resources](../src/site/resources).

The User's and the Developer's Guide
are maintained as immediately on GitHub readable Markdown files in the directory [docs](.).
See [GitHub Flavored Markdown Spec](https://github.github.com/gfm/).
This format is especially useful for being managed by a versioning system,
and for generating output for different targets,
for example web sites, and printed books.

You can edit it in a Markdown-aware text editor,
as in the open source [jEdit - Programmer's Text Editor](http://www.jedit.org/)
with it's [MarkdownPlugin](http://plugins.jedit.org/plugins/?MarkdownPlugin),
or with a dedicated Markdown editor, as [Typora](https://typora.io/).
The latter is much more comfortable.
It is free for non-commercial use.