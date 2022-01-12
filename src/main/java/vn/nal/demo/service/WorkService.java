package vn.nal.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.nal.demo.model.Work;
import vn.nal.demo.repository.WorkRepository;

@Service
@AllArgsConstructor
public class WorkService {

    private final WorkRepository workRepository;

    public Work save(Work work) {
        return workRepository.save(work);
    }

    public Work findById(Integer id) {
        return workRepository.findById(id).orElse(new Work());
    }

    public Page<Work> getList(int page, int size, String orderBy, String orderType) {
        Pageable pageable = PageRequest.of(page, size);

        if (orderBy != null && !orderBy.isEmpty()) {
            pageable = PageRequest.of(page, size,
                    orderType != null && orderType.equals("des") ? Sort.by(orderBy).descending() : Sort.by(orderBy));
        }

        return workRepository.findAll(pageable);
    }

    public void delete(Integer id) {
        workRepository.deleteById(id);
    }
}
