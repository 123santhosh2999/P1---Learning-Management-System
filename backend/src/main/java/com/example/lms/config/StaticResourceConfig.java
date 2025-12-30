package com.example.lms.config;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

  private final Path uploadsDir;

  public StaticResourceConfig(@Value("${app.uploads-dir}") String uploadsDir) {
    this.uploadsDir = Path.of(uploadsDir);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
      .addResourceHandler("/uploads/**")
      .addResourceLocations(uploadsDir.toUri().toString());
  }
}
