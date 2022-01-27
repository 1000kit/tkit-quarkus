/*
 * Copyright 2019 1000kit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.quarkus.jpa.deployment;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.BiFunction;

/**
 * The abstract  entity builder enhancer
 */
public class EntityServiceBuilderEnhancer implements BiFunction<String, ClassVisitor, ClassVisitor> {

    /**
     * The entity class.
     */
    private String entityClass;

    /**
     * The entity name.
     */
    private String entityName;

    /**
     * The ID attribute name.
     */
    private String idAttributeName;

    /**
     * The default constructor.
     *
     * @param entityName  entity name.
     * @param entityClass entity class.
     */
    public EntityServiceBuilderEnhancer(String entityName, String entityClass, String idAttributeName) {
        this.entityClass = entityClass;
        this.entityName = entityName;
        this.idAttributeName = idAttributeName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ClassVisitor apply(String className, ClassVisitor outputClassVisitor) {
        return new EntityServiceBuilderEnhancerClassVisitor(className, outputClassVisitor, entityName, entityClass, idAttributeName);
    }

    /**
     * The entity builder enhancer class visitor.
     */
    static class EntityServiceBuilderEnhancerClassVisitor extends ClassVisitor {
        /**
         * The entity class.
         */
        private String entityClass;
        /**
         * The entity name.
         */
        private String entityName;

        /**
         * The ID attribute name.
         */
        private String idAttributeName;

        public EntityServiceBuilderEnhancerClassVisitor(String className, ClassVisitor outputClassVisitor, String entityName, String entityClass, String idAttributeName) {
            super(Opcodes.ASM7, outputClassVisitor);
            this.entityClass = entityClass.replace('.', '/');
            this.entityName = entityName;
            this.idAttributeName = idAttributeName;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void visitEnd() {

            MethodVisitor mv1 = super.visitMethod(Opcodes.ACC_PROTECTED | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE,
                    "getEntityClass",
                    "()Ljava/lang/Class;",
                    null,
                    null);
            mv1.visitCode();
            mv1.visitLdcInsn(Type.getType("L" + entityClass + ";"));
            mv1.visitInsn(Opcodes.ARETURN);
            mv1.visitMaxs(0, 0);
            mv1.visitEnd();

            MethodVisitor mv = super.visitMethod(Opcodes.ACC_PROTECTED | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE,
                    "getEntityName",
                    "()Ljava/lang/String;",
                    null,
                    null);
            mv.visitCode();
            mv.visitLdcInsn(entityName);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            MethodVisitor ma = super.visitMethod(Opcodes.ACC_PROTECTED | Opcodes.ACC_SYNTHETIC | Opcodes.ACC_BRIDGE,
                    "getIdAttributeName",
                    "()Ljava/lang/String;",
                    null,
                    null);
            ma.visitCode();
            ma.visitLdcInsn(idAttributeName);
            ma.visitInsn(Opcodes.ARETURN);
            ma.visitMaxs(0, 0);
            ma.visitEnd();
            super.visitEnd();
        }
    }
}
