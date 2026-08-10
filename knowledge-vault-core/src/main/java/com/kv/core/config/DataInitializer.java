package com.kv.core.config;

import com.kv.core.entity.Category;
import com.kv.core.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("分类数据已存在，跳过初始化");
            return;
        }

        log.info("开始初始化行业分类数据...");

        List<String> names = List.of(
                "IT技术", "人工智能", "开发运维", "金融投资", "法律维权",
                "职场管理", "设计创作", "教育升学", "医疗健康", "实体行业",
                "手工文创", "思维认知", "公共政务"
        );

        for (int i = 0; i < names.size(); i++) {
            Category category = new Category();
            category.setName(names.get(i));
            category.setParentId(null);
            category.setLevel(1);
            category.setSortOrder(i);
            category = categoryRepository.save(category);
            category.setPath("/" + category.getId());
            categoryRepository.save(category);
        }

        log.info("行业分类数据初始化完成，共{}条", names.size());
    }
}
