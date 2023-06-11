package com.uniovi.sercheduler.dto;

/** Defines a file of a task. */
public class TaskFile {

  private String name;
  private Direction link;
  private Long size;

  /**
   * Full constructor.
   *
   * @param name The name of the file.
   * @param link If the file is an input or output.
   * @param size The size of the file in bits.
   */
  public TaskFile(String name, Direction link, Long size) {
    this.name = name;
    this.link = link;
    this.size = size;
  }

  public Long getSize() {
    return size;
  }
}
