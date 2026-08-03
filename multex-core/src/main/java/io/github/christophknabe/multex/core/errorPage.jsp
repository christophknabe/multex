<%@ page isErrorPage="true" %>

<HTML>
<!--
  Copyright (c) 2002 Christoph Knabe
  Useful as JSP error page in conjunction with use of the exception framework MulTEx.
-->

<BODY BGCOLOR="#FFDDDD">


The following exception occured when executing your request:

<P><FONT SIZE="+1"><B>
<%
final StringBuffer buffer = new StringBuffer();
multex.MsgText.appendMessageChain(buffer, exception, "\n<BR>");
out.println(buffer);
%>
</B></FONT>

<HR>
The stack trace follows:
<pre>
<%= multex.Msg.getStackTrace(exception) %>
</pre>

<h3>Here are useful request informations:</h3>

<!-- The Request and Response objects conform to the Servlet API -->

<table border=1>
  <tr><td>Query string        <td><%= request.getQueryString() %>
  <%{

    final java.util.Enumeration e = request.getHeaderNames();
    while (e.hasMoreElements()) {
      final String name = (String) e.nextElement();
      %><tr><td><%= name %><td><%= request.getHeader(name) %>
      <%
    }

  }%>
</table>

</BODY>
</HTML>
