package com.fno.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LoadBalanceType {
    RANDOM(1, "random"),
    ROUNDROBIN(2, "roundRobin"),
    CONSISTENTHASH(3, "consistentHash"),
    LEASTACTIVE(4, "leastActive");
    private Integer code;
    private String type;

    public static LoadBalanceType getLoadBalanceType(String type) {
        for (LoadBalanceType loadBalanceType : LoadBalanceType.values()) {
            if (loadBalanceType.getType().equals(type)) {
                return loadBalanceType;
            }
        }
        return LoadBalanceType.RANDOM;
    }
}
