package com.cloudkitchens.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enum.
 */
public enum Temp {
  @JsonProperty("frozen")
  FROZEN,
  @JsonProperty("cold")
  COLD,
  @JsonProperty("hot")
  HOT
}
