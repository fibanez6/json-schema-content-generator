package com.fibanez.jsonschema.content.generator.util;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtils {

    /**
     * @return a collection of the classes T
     */
    public static <T> Stream<Class<? extends T>> getSubClassesOf(Class<T> clazz) {
        String packageName = clazz.getPackage().getName();
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends T>> set = reflections.getSubTypesOf(clazz);
        return set.stream()
                .filter(not(ReflectionUtils::isAbstractClass).and(not(Class::isInterface)));
    }

    public static boolean isAbstractClass(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static Type getClassType(Class<?> clazz) {
        try {
            Type type;
            if (clazz.getGenericInterfaces().length > 0) {
                type = clazz.getGenericInterfaces()[0];
            } else {
                type = clazz.getGenericSuperclass();
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments()[0];
        } catch (Exception e) {
            throw new GeneratorException(e, "Could not find out the class type for " + clazz.getCanonicalName());
        }
    }

    /**
     * @param clazz generic class
     * @param <T>   type
     * @return A new instance of the class
     */
    public static <T> T getDefaultInstanceOf(Class<T> clazz, Context ctx) {
        try {
            @SuppressWarnings("unchecked")
            Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
            for (Constructor<T> c : constructors) {
                Class<?>[] parameterTypes = c.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0] == Context.class) {
                    c.setAccessible(true);
                    return c.newInstance(ctx);
                }
            }

            constructors[0].setAccessible(true);
            return constructors[0].newInstance();
        } catch (Exception e) {
            throw new GeneratorException(e, "Could not create a instance of " + clazz.getCanonicalName());
        }
    }

}
