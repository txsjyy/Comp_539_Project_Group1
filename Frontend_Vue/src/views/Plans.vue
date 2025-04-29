<!-- Subscription plans view component -->
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
  
  // Interface for subscription plan
  interface Plan {
    id: number;
    name: string;
    price: string;
    features: string[];
  }
  
  // Available subscription plans
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
  
  // Handle plan selection
  const buyPlan = (plan: Plan) => {
    // TODO: Integrate payment processing or redirect to payment page
    alert(`You have selected the ${plan.name} plan. Payment integration coming soon!`);
  };
  </script>
  
  <style scoped>
  /* Plans container layout */
  .plans-container {
    text-align: center;
    padding: 2rem;
  }
  
  /* Plans grid layout */
  .plans-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    justify-content: center;
    margin-top: 2rem;
  }
  
  /* Plan card styling */
  .plan-card {
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 20px;
    width: 300px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
  }
  
  /* Plan card hover effect */
  .plan-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  }
  
  /* Plan title styling */
  .plan-title {
    font-size: 24px;
    margin-bottom: 10px;
    color: #1d72b8;
  }
  
  /* Plan price styling */
  .plan-price {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 15px;
    color: #333;
  }
  
  /* Plan features list styling */
  .plan-features {
    list-style: none;
    padding: 0;
    margin-bottom: 20px;
    text-align: left;
  }
  
  /* Plan feature item styling */
  .plan-features li {
    margin-bottom: 8px;
    color: #555;
  }
  
  /* Buy button styling */
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
  
  /* Buy button hover state */
  .buy-button:hover {
    background-color: #145a8d;
  }
  </style>
  