// package com.wenge.model.workflow.component.variable.test;
//
// import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
// import com.wenge.model.workflow.component.file.File;
// import com.wenge.model.workflow.component.variable.Variable;
// import com.wenge.model.workflow.component.variable.VariablePool;
// import com.wenge.model.workflow.enums.MetaParamEnum;
// import com.wenge.model.workflow.factory.VariableFactory;
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Optional;
//
// /**
//  * 测试string类型
//  */
// public class SegmentTest {
//
//     public static void main(String[] args) {
//         VariablePool variablePool = new VariablePool(new HashMap<>(), new HashMap<>(), new ArrayList<>(), null);
//         String nodeId = "testNodeId";
//         String paramName = "testParam";
//         Variable variable = arrayFileTest(variablePool, nodeId, paramName);
//         System.out.println("text\n" + variable.getSegments().text());
//         System.out.println("markdown\n" + variable.getSegments().markdown());
//         System.out.println("log\n" + variable.getSegments().log());
//         System.out.println("value\n" + variable.getSegments().getValue());
// //        System.out.println(variable.getSegments().size());
//     }
//
//     public static Variable strTest(VariablePool variablePool, String nodeId, String paramName) {
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.STRING.getName()
//                 , nodeId, paramName, "testValue");
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable intTest(VariablePool variablePool, String nodeId, String paramName) {
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.INTEGER.getName()
//                 , nodeId, paramName, 1);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable boolTest(VariablePool variablePool, String nodeId, String paramName) {
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.BOOLEAN.getName()
//                 , nodeId, paramName, true);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable objectTest(VariablePool variablePool, String nodeId, String paramName) {
//         File file = new File();
//         file.setFilename("test");
//         file.setExtension("doc");
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.OBJECT.getName()
//                 , nodeId, paramName, file);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable fileTest(VariablePool variablePool, String nodeId, String paramName) {
//         File file = new File();
//         file.setFilename("test");
//         file.setExtension("doc");
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.FILE.getName()
//                 , nodeId, paramName, file);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable arrayStrTest(VariablePool variablePool, String nodeId, String paramName) {
//         List<String> list = new ArrayList<>();
//         list.add("test1");
//         list.add("test2");
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.ARRAY_STR.getName()
//                 , nodeId, paramName, list);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable arrayIntTest(VariablePool variablePool, String nodeId, String paramName) {
//         List<Long> list = new ArrayList<>();
//         list.add(1L);
//         list.add(3L);
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.ARRAY_INT.getName()
//                 , nodeId, paramName, list);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable arrayBooleanTest(VariablePool variablePool, String nodeId, String paramName) {
//         List<Boolean> list = new ArrayList<>();
//         list.add(true);
//         list.add(false);
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.ARRAY_BOOL.getName()
//                 , nodeId, paramName, list);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable arrayObjectTest(VariablePool variablePool, String nodeId, String paramName) {
//         List<File> list = new ArrayList<>();
//         File file1 = new File();
//         file1.setFilename("文件1");
//         File file2 = new File();
//         file2.setFilename("文件2");
//         list.add(file1);
//         list.add(file2);
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.ARRAY_OBJ.getName(), nodeId, paramName, list);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
//
//     public static Variable arrayFileTest(VariablePool variablePool, String nodeId, String paramName) {
//         List<File> list = new ArrayList<>();
//         File file1 = new File();
//         file1.setFilename("文件1");
//         File file2 = new File();
//         file2.setFilename("文件2");
//         list.add(file1);
//         list.add(file2);
//         Variable variable = VariableFactory.createVariable(MetaParamEnum.ARRAY_FILE.getName(), nodeId, paramName, list);
//         variablePool.add(variable);
//
//         Optional<Variable> variable1 = variablePool.get(Lists.newArrayList(nodeId, paramName));
//         return variable1.get();
//     }
// }
