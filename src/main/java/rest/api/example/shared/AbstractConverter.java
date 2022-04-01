package rest.api.example.shared;

import java.util.List;
import java.util.stream.Collectors;

public interface AbstractConverter<E, D> {

    E toEntity(D d);

    D toDTO(E e);

    default List<E> toEntities(List<D> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    default List<D> toDTOs(List<E> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
