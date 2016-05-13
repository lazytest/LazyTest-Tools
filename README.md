# LazyTest-Tools

This is a set of tools/utils help to utilize your testing work.

Includes:

<b>DataProvider</b><br>
--CsvDataProvider<br>
--IExcelDataProvider<br>
The two can be used to import/export to/from DB in CSV or Excel format.

<b>Utils</b><br>
--CsvUtil<br>
--DateUtil<br>
--DBUtil<br>
--FileUtil<br>
--HtmlUtil<br>
--HttpUtil<br>
--JsonUtil<br>
--XmlUtil<br>
--......<br>
These utils can be used to deal with data in common formats.

<b>AssertUtils</b><br>
--CsvAssertUtil<br>
--HashMapAssertUtil<br>
--HtmlAssertUtil<br>
--JsonAssertUtil<br>
--XmlAssertUtil<br>
--......<br>
These assert utils can be used to assert(testng) results with data in common formats.

To use this tool, please include following repo:

    <repositories>
    	<repository>
    		<id>lazytest</id>
    		<url>https://raw.githubusercontent.com/lazytest/mvn-repo/master/</url>
    	</repository>
    </repositories>

and add following dependency:

    <dependency>
        <groupId>lazy.test</groupId>
        <artifactId>lazy-test-tools</artifactId>
        <version>1.0.0</version>
    </dependency>
    
For more info, please refer to the apis.
