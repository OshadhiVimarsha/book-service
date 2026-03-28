package lk.ijse.eca.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class BookResponseDTO {

    private String id;
    private String title;
    private String author;
    private String category;
    private Integer publishedYear;
    private Boolean available;
    private String coverImage; // URL or filename
}