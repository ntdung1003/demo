package vn.nal.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import vn.nal.demo.model.Work;
import vn.nal.demo.service.WorkService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("work")
@AllArgsConstructor
public class WorkController {

    private final WorkService workService;

    @PostMapping
    public ResponseEntity<Work> add(@Valid @RequestBody Work work) {
        return ResponseEntity.ok(workService.save(work));
    }

    @GetMapping
    public ResponseEntity<List<Work>> list(@RequestParam(defaultValue = "0", required = false) int page,
                                           @RequestParam(defaultValue = "10", required = false) int size,
                                           @RequestParam(defaultValue = "", required = false) String orderBy,
                                           @RequestParam(defaultValue = "asc", required = false) String orderType) {

        return ResponseEntity.ok(workService.getList(page, size, orderBy, orderType).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Work> findById(@PathVariable int id) {
        return ResponseEntity.ok(workService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        workService.delete(id);

        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
