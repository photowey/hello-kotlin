/*
 * Copyright (c) 2026-present The Hello-Kotlin Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kotlin.jvm.functions;

/**
 * A functional interface that takes 23 parameters and returns a result.
 * Extends the Kotlin function limit (which stops at Function22).
 *
 * @param <P1>  the type of the first parameter
 * @param <P2>  the type of the second parameter
 *              ...
 * @param <P23> the type of the twenty-third parameter
 * @param <R>   the type of the result
 * @author photowey
 * @version 1.0.0
 * @since 2026/01/07
 */
@FunctionalInterface
public interface FunctionN23<
    P1, P2, P3, P4, P5, P6, P7, P8, P9, P10,
    P11, P12, P13, P14, P15, P16, P17, P18, P19, P20,
    P21, P22, P23, R> {

    /**
     * Invokes the function with 23 arguments.
     */
    R invoke(
        P1 p1, P2 p2, P3 p3, P4 p4, P5 p5,
        P6 p6, P7 p7, P8 p8, P9 p9, P10 p10,
        P11 p11, P12 p12, P13 p13, P14 p14, P15 p15,
        P16 p16, P17 p17, P18 p18, P19 p19, P20 p20,
        P21 p21, P22 p22, P23 p23
    );
}
