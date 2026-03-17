import { flushPromises, mount } from '@vue/test-utils';
import { describe, expect, it, vi } from 'vitest';
import UserLeaveApply from './UserLeaveApply.vue';

vi.mock('../../api/leave', () => ({
  applyLeaveApi: vi.fn().mockResolvedValue({ id: 123 })
}));

describe('UserLeaveApply', () => {
  it('submit should show success message', async () => {
    const wrapper = mount(UserLeaveApply);
    const inputs = wrapper.findAll('input');
    await inputs[0].setValue('病假');
    await inputs[1].setValue('2026-03-20T08:00:00');
    await inputs[2].setValue('2026-03-20T18:00:00');
    await wrapper.find('textarea').setValue('测试原因');
    const submitBtn = wrapper.findAll('button').find((b) => b.text() === '提交申请');
    await submitBtn.trigger('click');
    await flushPromises();
    expect(wrapper.text()).toContain('提交成功');
  });
});
