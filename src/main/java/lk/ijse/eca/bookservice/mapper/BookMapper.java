package lk.ijse.eca.bookservice.mapper;

import lk.ijse.eca.bookservice.dto.BookRequestDTO;
import lk.ijse.eca.bookservice.dto.BookResponseDTO;
import lk.ijse.eca.bookservice.entity.Book;
import org.mapstruct.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class BookMapper {

    @Mapping(target = "coverImage", expression = "java(buildCoverImageUrl(book))")
    public abstract BookResponseDTO toResponseDto(Book book);

    @Mapping(target = "coverImage", ignore = true)
    public abstract Book toEntity(BookRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    public abstract void updateEntity(BookRequestDTO dto, @MappingTarget Book book);

    protected String buildCoverImageUrl(Book book) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/books/{id}/cover-image")
                .buildAndExpand(book.getId())
                .toUriString();
    }
}