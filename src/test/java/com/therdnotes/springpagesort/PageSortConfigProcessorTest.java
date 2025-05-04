package com.therdnotes.springpagesort;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;

public class PageSortConfigProcessorTest {

    @Test
    void shouldFailWhenDefaultSortByIsNotInValidSortFields() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.InvalidController",
                "package com.example;\n" +
                        "import com.therdnotes.springpagesort.PageSortConfig;\n" +
                        "import org.springframework.web.bind.annotation.RestController;\n" +
                        "import org.springframework.web.bind.annotation.GetMapping;\n" +
                        "@RestController\n" +
                        "public class InvalidController {\n" +
                        "    @GetMapping(\"/test\")\n" +
                        "    @PageSortConfig(defaultSortBy = \"invalid\", validSortFields = {\"name\", \"days\"})\n" +
                        "    public void testMethod() {}\n" +
                        "}\n"
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new PageSortConfigProcessor())
                .compile(source);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("defaultSortBy must be one of the validSortFields");
    }

    @Test
    void shouldPassWhenDefaultSortByIsInValidSortFields() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.ValidController",
                "package com.example;\n" +
                        "import com.therdnotes.springpagesort.PageSortConfig;\n" +
                        "import org.springframework.web.bind.annotation.RestController;\n" +
                        "import org.springframework.web.bind.annotation.GetMapping;\n" +
                        "@RestController\n" +
                        "public class ValidController {\n" +
                        "    @GetMapping(\"/test\")\n" +
                        "    @PageSortConfig(defaultSortBy = \"name\", validSortFields = {\"name\", \"days\"})\n" +
                        "    public void testMethod() {}\n" +
                        "}\n"
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new PageSortConfigProcessor())
                .compile(source);

        assertThat(compilation).succeeded();
    }

    @Test
    void shouldPassWhenDefaultSortByIsEmpty() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.EmptyDefaultController",
                "package com.example;\n" +
                        "import com.therdnotes.springpagesort.PageSortConfig;\n" +
                        "import org.springframework.web.bind.annotation.RestController;\n" +
                        "import org.springframework.web.bind.annotation.GetMapping;\n" +
                        "@RestController\n" +
                        "public class EmptyDefaultController {\n" +
                        "    @GetMapping(\"/test\")\n" +
                        "    @PageSortConfig(defaultSortBy = \"\", validSortFields = {\"name\", \"days\"})\n" +
                        "    public void testMethod() {}\n" +
                        "}\n"
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new PageSortConfigProcessor())
                .compile(source);

        assertThat(compilation).succeeded();
    }

    @Test
    void shouldPassForControllerAnnotation() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.RegularController",
                "package com.example;\n" +
                        "import com.therdnotes.springpagesort.PageSortConfig;\n" +
                        "import org.springframework.stereotype.Controller;\n" +
                        "import org.springframework.web.bind.annotation.GetMapping;\n" +
                        "@Controller\n" +
                        "public class RegularController {\n" +
                        "    @GetMapping(\"/test\")\n" +
                        "    @PageSortConfig(defaultSortBy = \"name\", validSortFields = {\"name\", \"days\"})\n" +
                        "    public void testMethod() {}\n" +
                        "}\n"
        );

        Compilation compilation = Compiler.javac()
                .withProcessors(new PageSortConfigProcessor())
                .compile(source);

        assertThat(compilation).succeeded();
    }
}