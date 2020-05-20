package com.github.bradleywood.injex.info;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InjexField extends InjexElement {

    private String srcClass;
    private String name;
    private String desc;

}
