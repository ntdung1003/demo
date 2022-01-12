package vn.nal.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.nal.demo.enums.Status;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Name is mandatory")
    @Size(min=1, max=1000)
    private String workName;

    @NotNull(message = "Starting Date is mandatory")
    private LocalDate startingDate;

    @NotNull(message = "Ending Date is mandatory")
    private LocalDate endingDate;

    @NotNull(message = "Status is mandatory")
    private Status status;

    @AssertTrue(message = "Field 'endingDate' should be later than 'startingDate'")
    private boolean isEndingDateAfterStartingDate() {
        if (startingDate != null && endingDate != null) {
            return startingDate.isBefore(endingDate);
        }

        return false;
    }
}
