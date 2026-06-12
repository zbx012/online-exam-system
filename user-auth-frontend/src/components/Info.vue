<template>
    <div>
       <p v-if="loading">正在加载用户信息...</p>
    </div>
</template>
<script setup lang="ts">
    import { ref, onMounted } from 'vue';
    import { useRouter } from 'vue-router';
    import request from '@/utils/request';
    
    const router = useRouter();
    const loading = ref(true);

    const checkUserInfo = async () => {
  try {
    const currentUser = localStorage.getItem('currentUser');
    if (currentUser) {
      // 验证用户存在后跳转
      router.push('/student/home');
    }
  } catch (error) {
    console.error('检查用户信息失败:', error);
    loading.value = false;
  }
};

onMounted(() => {
  checkUserInfo();
});
</script>

