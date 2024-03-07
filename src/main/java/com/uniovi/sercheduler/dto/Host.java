package com.uniovi.sercheduler.dto;

/** Defines a server with the main specs. */
public class Host {

  /**
   * Full constructor.
   *
   * @param name Name of the host.
   * @param flops Number of operations per second.
   * @param diskSpeed disk speed in bits.
   * @param networkSpeed network speed in bits.
   * @param energyCost The cost per second of the host.
   */
  public Host(String name, Long flops, Long diskSpeed, Long networkSpeed, Double energyCost) {
    this.name = name;
    this.flops = flops;
    this.diskSpeed = diskSpeed;
    this.networkSpeed = networkSpeed;
    this.energyCost = energyCost;
  }

  private String name;

  private Long flops;

  private Long diskSpeed;

  private Long networkSpeed;

  private Double energyCost;


  public String getName() {
    return name;
  }

  public Long getFlops() {
    return flops;
  }

  public Long getDiskSpeed() {
    return diskSpeed;
  }

  public Long getNetworkSpeed() {
    return networkSpeed;
  }

  public Double getEnergyCost() {
    return energyCost;
  }
}
