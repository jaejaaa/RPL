package com.satupadu.satupaduerp.aspect;

import com.satupadu.satupaduerp.Session;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// AOP: pencatatan aktivitas (logging) dipisahkan dari kode controller.
// Kode ini otomatis berjalan setiap kali method di package controller dipanggil.
@Aspect
@Component
public class LoggingAspect {

    // semua method public di controller, kecuali getter
    @Around("execution(public * com.satupadu.satupaduerp.controller..*(..)) && !execution(* get*(..))")
    public Object catatAktivitas(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getDeclaringType().getSimpleName()
                      + "." + joinPoint.getSignature().getName() + "()";
        String user = Session.getUser() == null ? "-" : Session.getUser().getUsername();
        long mulai = System.currentTimeMillis();

        try {
            Object hasil = joinPoint.proceed();   // menjalankan method aslinya
            System.out.println("[AOP] " + user + " | " + method + " berhasil ("
                + (System.currentTimeMillis() - mulai) + " ms)");
            return hasil;
        } catch (Throwable e) {
            System.out.println("[AOP] " + user + " | " + method + " gagal: " + e.getMessage());
            throw e;
        }
    }
}
