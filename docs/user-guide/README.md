# User's Guide for MulTEx - The Multi Tier Exception Handling  Framework
Christoph Knabe, 2022-04-28

The main goal of this user's guide is to tell, how to quickly introduce robust and diagnostic exception handling and reporting into a Java software system.

These hints are easier to follow, if you are writing a software system from scratch, but can be applied onto existing software, as well.

You should introduce it according to these priorities:

1. [Introduce central exception reporting](1.central-exception-reporting.md) in the highest layer
2. Assure exception propagation to the highest possible level
3. Provide all exceptions with diagnostic parameters
4. Provide natural message texts for all execptions

The last three numbers are covered in the document
[How to introduce/use MulTEx in the API layers](2.propagate-parameterize-textize.md)