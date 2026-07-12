package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) // 👈 Indique que cette annotation se place sur les MÉTHODES
public @interface UrlMapping {
    String value(); // 👈 Permet d'écrire @UrlMapping("votre-url")
}