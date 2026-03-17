<template>
  <div class="card">
    <h3 style="margin-top:0;">角色与权限</h3>
    <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px;">
      <input v-model="createForm.roleCode" placeholder="角色编码" />
      <input v-model="createForm.roleName" placeholder="角色名称" />
      <input v-model="createForm.remark" placeholder="备注" />
      <button v-permission="'role:update'" @click="createRole">新增角色</button>
      <button @click="loadData">刷新</button>
    </div>

    <table border="1" cellpadding="8" cellspacing="0" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>角色编码</th>
          <th>角色名称</th>
          <th>备注</th>
          <th>权限分配</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="role in roles" :key="role.id">
          <td>{{ role.id }}</td>
          <td>{{ role.roleCode }}</td>
          <td>{{ role.roleName }}</td>
          <td>{{ role.remark }}</td>
          <td>
            <div style="display:flex;gap:6px;flex-wrap:wrap;max-width:580px;">
              <label v-for="p in permissions" :key="`${role.id}-${p.id}`" style="font-size:12px;">
                <input
                  type="checkbox"
                  :checked="selectedPerms[role.id]?.includes(p.id)"
                  @change="toggle(role.id, p.id, $event.target.checked)"
                />
                {{ p.permCode }}
              </label>
            </div>
          </td>
          <td style="display:flex;gap:6px;">
            <button v-permission="'role:update'" @click="saveRolePerms(role.id)">保存权限</button>
            <button v-permission="'role:update'" @click="removeRole(role.id)">删除角色</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import {
  createRoleApi,
  deleteRoleApi,
  listAllPermissionsApi,
  listRolePermissionsApi,
  listRolesApi,
  updateRolePermissionsApi
} from '../../api/adminRoles';

const roles = ref([]);
const permissions = ref([]);
const selectedPerms = reactive({});
const createForm = reactive({
  roleCode: '',
  roleName: '',
  remark: ''
});

const loadData = async () => {
  roles.value = await listRolesApi();
  permissions.value = await listAllPermissionsApi();
  for (const role of roles.value) {
    selectedPerms[role.id] = await listRolePermissionsApi(role.id);
  }
};

const toggle = (roleId, permId, checked) => {
  if (!selectedPerms[roleId]) selectedPerms[roleId] = [];
  const set = new Set(selectedPerms[roleId]);
  if (checked) set.add(permId);
  else set.delete(permId);
  selectedPerms[roleId] = Array.from(set);
};

const saveRolePerms = async (roleId) => {
  await updateRolePermissionsApi(roleId, selectedPerms[roleId] || []);
};

const createRole = async () => {
  await createRoleApi(createForm);
  createForm.roleCode = '';
  createForm.roleName = '';
  createForm.remark = '';
  await loadData();
};

const removeRole = async (roleId) => {
  await deleteRoleApi(roleId);
  await loadData();
};

onMounted(loadData);
</script>
