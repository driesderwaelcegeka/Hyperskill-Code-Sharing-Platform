<html>
<header>
<title>Latest</title>
</header>
<body>
<#list result as result>

<pre id="code_snippet">  ${result.getCode()}  </pre>
<span id="load_date"> ${result.getDate()} </span>

</#list>
</body>
</html>

