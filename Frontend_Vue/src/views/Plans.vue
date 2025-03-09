<!-- src/views/Plans.vue -->
<template>
    <div class="plans-container">
      <h1>Our Plans</h1>
      <p>Choose the best plan that fits your needs.</p>
      <div class="plans-grid">
        <div v-for="plan in plans" :key="plan.id" class="plan-card">
          <h2 class="plan-title">{{ plan.name }}</h2>
          <p class="plan-price">{{ plan.price }}</p>
          <ul class="plan-features">
            <li v-for="(feature, index) in plan.features" :key="index">{{ feature }}</li>
          </ul>
          <button @click="buyPlan(plan)" class="buy-button">Buy Now</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue';
  
  interface Plan {
    id: number;
    name: string;
    price: string;
    features: string[];
  }
  
  const plans = ref<Plan[]>([
    {
      id: 1,
      name: 'Basic',
      price: '$9.99/month',
      features: [
        'Basic Analytics',
        'Limited Short URLs',
        'Standard Support'
      ]
    },
    {
      id: 2,
      name: 'Pro',
      price: '$19.99/month',
      features: [
        'Advanced Analytics',
        'Unlimited Short URLs',
        'Custom Domain',
        'Priority Support'
      ]
    },
    {
      id: 3,
      name: 'Enterprise',
      price: 'Contact Us',
      features: [
        'All Pro features',
        'Dedicated Account Manager',
        'Custom Integrations',
        'White Labeling'
      ]
    }
  ]);
  
  const buyPlan = (plan: Plan) => {
    // 集成支付逻辑，或者跳转到支付页面，目前只是弹出提示
    alert(`You have selected the ${plan.name} plan. Payment integration coming soon!`);
  };
  </script>
  
  <style scoped>
  .plans-container {
    text-align: center;
    padding: 2rem;
  }
  
  .plans-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    justify-content: center;
    margin-top: 2rem;
  }
  
  .plan-card {
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 20px;
    width: 300px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
  }
  
  .plan-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  }
  
  .plan-title {
    font-size: 24px;
    margin-bottom: 10px;
    color: #1d72b8;
  }
  
  .plan-price {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 15px;
    color: #333;
  }
  
  .plan-features {
    list-style: none;
    padding: 0;
    margin-bottom: 20px;
    text-align: left;
  }
  
  .plan-features li {
    margin-bottom: 8px;
    color: #555;
  }
  
  .buy-button {
    background-color: #1d72b8;
    color: white;
    border: none;
    padding: 12px;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.3s;
    font-size: 16px;
    width: 100%;
  }
  
  .buy-button:hover {
    background-color: #145a8d;
  }
  </style>
  