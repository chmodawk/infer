/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package codetoanalyze.java.nullsafe_annotation_graph;

import javax.annotation.Nullable;

public class AnnotationGraph {
  public String fieldA;
  public String fieldB;
  public @Nullable String fieldC;
  public String fieldD;

  // methodA() depends on `p` and on `fieldD`
  private String methodA(String p, boolean flag) {
    // fieldA depends on p
    fieldA = p;
    if (flag) {
      return p;
    } else {
      return fieldD;
    }
  }

  // methodB() depends on methodA()'s return
  private String methodB() {
    return methodA("", true);
  }

  public String methodC() {
    String a = methodB();
    // fieldC depends on methodB()
    fieldC = a;

    // return does NOT depend on methodB(): already checked for null
    if (a != null) {
      return a;
    }

    return "";
  }

  private void methodD() {
    // fieldB depends on fieldA
    fieldB = fieldA;
  }

  private void methodE() {
    // violation for fieldD
    SomeExternalClass.acceptsNull(fieldD);
    // violation for fieldD
    fieldD.toString();
    if (fieldD != null) {
      // no violation for fieldD
      SomeExternalClass.acceptsNull(fieldD);
    }
    // no violation for fieldB
    SomeExternalClass.doesNotAcceptNull(fieldB);

    if (methodC() != null) {
      methodC().toString(); // no violation for methodC
    }
  }

  private void methodF() {
    // violation for fieldA
    fieldA.toString();

    methodC().toString(); // violation for methodC
  }
}

class SomeExternalClass {
  public static void acceptsNull(@Nullable String a) {}

  public static void doesNotAcceptNull(String a) {}
}