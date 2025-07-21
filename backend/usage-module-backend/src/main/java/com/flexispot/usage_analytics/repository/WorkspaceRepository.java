package com.flexispot.usage_analytics.repository;

import com.flexispot.usage_analytics.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}
