import { flushPromises, mount } from '@vue/test-utils';
import { describe, expect, it, vi } from 'vitest';
import UserLeaveApply from './UserLeaveApply.vue';
import { ElMessage } from 'element-plus';

const pushMock = vi.fn();
const applyLeaveApiMock = vi.fn();

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: pushMock })
}));

vi.mock('../../api/leave', () => ({
  applyLeaveApi: (...args) => applyLeaveApiMock(...args)
}));

vi.mock('../../api/file', () => ({
  uploadFileApi: vi.fn()
}));

vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn()
  }
}));

describe('UserLeaveApply', () => {
  it('submit should call API and navigate to progress page', async () => {
    applyLeaveApiMock.mockResolvedValue({ id: 123 });

    const wrapper = mount(UserLeaveApply, {
      global: {
        stubs: {
          'el-row': { template: '<div><slot /></div>' },
          'el-col': { template: '<div><slot /></div>' },
          'el-card': { template: '<div><slot name="header"></slot><slot></slot></div>' },
          'el-form': { template: '<form><slot /></form>' },
          'el-form-item': { template: '<div><slot /></div>' },
          'el-select': { template: '<div><slot /></div>' },
          'el-option': true,
          'el-date-picker': { template: '<input />' },
          'el-input': { template: '<input />' },
          'el-upload': { template: '<div><slot /></div>' },
          'el-button': { template: '<button @click="$emit(\'click\')"><slot /></button>' }
        }
      }
    });

    wrapper.vm.form.leaveType = '病假';
    wrapper.vm.form.startTime = '2026-03-20 08:00:00';
    wrapper.vm.form.endTime = '2026-03-20 18:00:00';
    wrapper.vm.form.reason = '测试原因';
    wrapper.vm.formRef = {
      validate: vi.fn().mockResolvedValue(true),
      clearValidate: vi.fn()
    };

    await wrapper.vm.submit();
    await flushPromises();

    expect(applyLeaveApiMock).toHaveBeenCalledWith(expect.objectContaining({
      leaveType: '病假',
      reason: '测试原因'
    }));
    expect(ElMessage.success).toHaveBeenCalled();
    expect(pushMock).toHaveBeenCalledWith('/user/leave-progress');
  });
});
