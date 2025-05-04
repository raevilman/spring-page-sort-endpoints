package com.therdnotes.springpagesort;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;


/**
 * Annotation processor for {@link PageSortConfig}.
 * <p>
 * This processor validates the configuration of the {@link PageSortConfig} annotation
 * at compile time. It ensures that the `defaultSortBy` field, if specified, is one of
 * the `validSortFields`. Additionally, it provides a note if `validSortFields` are
 * configured but `defaultSortBy` is not set.
 */
@SupportedAnnotationTypes("com.therdnotes.springpagesort.PageSortConfig")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class PageSortConfigProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(PageSortConfig.class)) {
            PageSortConfig config = element.getAnnotation(PageSortConfig.class);
            String defaultSortBy = config.defaultSortBy();
            String[] validSortFields = config.validSortFields();

            if (!defaultSortBy.isEmpty() && !Arrays.asList(validSortFields).contains(defaultSortBy)) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "defaultSortBy must be one of the validSortFields: " + Arrays.toString(validSortFields),
                        element
                );
            }

            // Check if validSortFields are specified but defaultSortBy is empty
            // This is optional and depends on your requirements
            if (defaultSortBy.isEmpty() && validSortFields.length > 0) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "You have configure validSortFields. Its a good practice to set defaultSortBy as well.",
                        element
                );
            }
        }
        return false;
    }
}