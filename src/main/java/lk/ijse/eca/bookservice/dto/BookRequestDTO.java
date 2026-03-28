package lk.ijse.eca.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BookRequestDTO {

    public interface OnCreate {}

    @NotBlank(groups = OnCreate.class, message = "Book ID is required")
    private String id;  // UUID වගේ unique id

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "Category is required")
    private String category;

    private Integer publishedYear;

    @NotNull(groups = OnCreate.class, message = "Cover image is required")
    private MultipartFile coverImage;

    private Boolean available = true;
}