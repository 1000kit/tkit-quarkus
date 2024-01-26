package org.tkit.quarkus.rs.context.runtime;

import java.lang.reflect.Method;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class RestContextTenantRecorder {

    static TenantAnnotationData DATA = new TenantAnnotationData();

    public void init(TenantAnnotationData data) {
        DATA = data;
    }

    public static boolean tenantExcludeClassMethod(Class<?> clazz, Method method) {
        // check class annotation
        if (DATA.isExcludeClass(clazz)) {
            return true;
        }
        // check method annotation
        return DATA.isExcludeMethod(clazz, method);
    }

}
