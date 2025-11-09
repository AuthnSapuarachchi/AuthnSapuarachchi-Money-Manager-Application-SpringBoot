package com.authcodelab.smartmoneymanageapp.repository;

import com.authcodelab.smartmoneymanageapp.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity , Long> {

    //select all categories by profile id
    List<CategoryEntity> findByProfileId(Long profileId);

    //select category by id and profile id
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    //select categories by type and profile id
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    //check if category exists by name and profile id
    Boolean existsByNameAndProfileId(String name, Long profileId);

}
