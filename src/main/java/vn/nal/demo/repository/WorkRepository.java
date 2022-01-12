package vn.nal.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nal.demo.model.Work;

@Repository
public interface WorkRepository extends JpaRepository<Work, Integer> {
}
