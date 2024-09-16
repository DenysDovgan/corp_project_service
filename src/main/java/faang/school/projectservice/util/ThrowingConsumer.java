package faang.school.projectservice.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void accept(T t) throws ChildrenNotFinishedException;

}

//public static <T> Consumer<T> wrap(ThrowingConsumer<T> throwingConsumer) {
//    return i -> {
//        try {
//            throwingConsumer.accept(i);
//        } catch (Exception e) {
//            throw new RuntimeException(e); // Оборачиваем исключение
//        }
//    };
//}
