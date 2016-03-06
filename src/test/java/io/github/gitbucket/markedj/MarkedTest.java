package io.github.gitbucket.markedj;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by takezoe on 15/09/19.
 */
public class MarkedTest {

    @Test
    public void testMarked() throws Exception {
        String md = loadResourceAsString("ldap_settings.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("ldap_settings.html");
        assertEquals(expect, result);
    }

    @Test
    public void testMarked2() throws Exception {
        String md = loadResourceAsString("gitbucket.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("gitbucket.html");
        assertEquals(expect, result);
    }

    @Test
    public void testMarked3() throws Exception {
        String md = loadResourceAsString("wikilink.md");
        String result = Marked.marked(md, new Options());
        String expect = loadResourceAsString("wikilink.html");
        assertEquals(expect, result);
//        Files.write(Paths.get("wikilink.html"), result.getBytes("UTF-8"));
    }

    @Test
    public void testOembed() throws Exception {
        String md = Marked.marked("[oembed https://speakerdeck.com/speakerdeck/introduction-to-speakerdeck]", new Options());
        String result = Marked.marked(md, new Options());
        assertEquals("<p><a class=\"oembed\" href=\"https://speakerdeck.com/speakerdeck/introduction-to-speakerdeck\">https://speakerdeck.com/speakerdeck/introduction-to-speakerdeck</a></p>\n", result);
    }

    @Test
    public void testAutolink() throws Exception {
        String result = Marked.marked("<takezoe@gmail.com>", new Options());
        assertEquals("<p><a href=\"mailto:takezoe@gmail.com\">takezoe@gmail.com</a></p>\n", result);
    }

    @Test
    public void testEm() throws Exception {
        {
            String result = Marked.marked("_aa__a__aa_", new Options());
            assertEquals("<p><em>aa<strong>a</strong>aa</em></p>\n", result);
        }
        {
            String result = Marked.marked("*aa*o*aa*", new Options());
            assertEquals("<p><em>aa</em>o<em>aa</em></p>\n", result);
        }
        {
            String result = Marked.marked("_aa__aa_", new Options());
            assertEquals("<p><em>aa__aa</em></p>\n", result);
        }
    }


    @Test
    public void testStackoverFlow() throws Exception {
        // Make sure StackOverflowError does not occur by em regular expression
        Marked.marked(loadResourceAsString("stackoverflow.txt"), new Options());
    }

    @Test
    public void testNptable() throws Exception {
        String result = Marked.marked(loadResourceAsString("nptable.md"), new Options());
        String expect = loadResourceAsString("nptable.html");
        assertEquals(expect, result);
    }

    @Test
    public void testBreaks() throws Exception {
        {
            String md = "first line\nsecond line";
            Options options = new Options();
            //options.setBreaks(false); // default is false
            //options.setGfm(true);     // default is true
            String result = Marked.marked(md, options);

            assertEquals("<p>first line\nsecond line</p>\n", result);
        }
        {
            String md = "first line\nsecond line";
            Options options = new Options();
            options.setBreaks(true);
            //options.setGfm(true); // default is true
            String result = Marked.marked(md, options);

            assertEquals("<p>first line<br>second line</p>\n", result);
        }
    }

    @Test
    public void testInvalidColumnTable() throws Exception {
        {
            String result = Marked.marked(loadResourceAsString("table.md"), new Options());
            assertEquals(loadResourceAsString("table.html"), result);
        }
    }

    @Test
    public void testFootnotes() throws Exception {
        String result = Marked.marked(loadResourceAsString("footnote.md"), new Options());
        String expect = loadResourceAsString("footnote.html");
        assertEquals(expect, result);
    }
    public void testCodeBlock() throws Exception {
        String result = Marked.marked(
                "    public class HelloWorld {\n" +
                "    }", new Options());
        assertEquals(
                "<pre><code>public class HelloWorld {\n" +
                "}\n" +
                "</code></pre>\n", result);
    }

    @Test
    public void testEmptyItemOfList() throws Exception {
        String result = Marked.marked(loadResourceAsString("empty_item_of_list.md"), new Options());
        assertEquals(loadResourceAsString("empty_item_of_list.html"), result);
    }

    @Test
    public void testParagraphSeparation() throws Exception {
        String result = Marked.marked(
                "Message A\n" +
                "- List A\n" +
                "- List B", new Options());

        assertEquals(
                "<p>Message A</p>\n" +
                "<ul>\n" +
                "<li>List A</li>\n" +
                "<li>List B</li>\n" +
                "</ul>\n", result);
    }

    private String loadResourceAsString(String path) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 8];
            int length = 0;
            while((length = in.read(buf)) != -1){
                out.write(buf, 0, length);
            }
            return new String(out.toByteArray(), "UTF-8");
        } finally {
            in.close();
        }
    }
}