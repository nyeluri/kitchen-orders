package com.cloudkitchen.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enum holding different Temp types
 */
public enum Temp {
  @JsonProperty("frozen")
  FROZEN,
  @JsonProperty("cold")
  COLD,
  @JsonProperty("hot")
  HOT
}
