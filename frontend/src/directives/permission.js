import { useAuthStore } from '../stores/auth';

export const permissionDirective = {
  mounted(el, binding) {
    const authStore = useAuthStore();
    const value = binding.value;
    const required = Array.isArray(value) ? value : [value];
    const ok = required.every((code) => authStore.hasPermission(code));
    if (!ok) {
      el.parentNode?.removeChild(el);
    }
  }
};
