import React from 'react';
import { ArrowUpRight, ArrowDownRight } from 'lucide-react';

const StatCard = ({ icon, label, value, trend, trendUp, color }) => {
    const colorClasses = {
        indigo: 'bg-indigo-50 text-indigo-600 border-indigo-100',
        emerald: 'bg-emerald-50 text-emerald-600 border-emerald-100',
        amber: 'bg-amber-50 text-amber-600 border-amber-100',
        rose: 'bg-rose-50 text-rose-600 border-rose-100',
    };

    return (
        <div className="group rounded-[32px] bg-white p-8 shadow-sm border border-slate-100 transition hover:shadow-xl hover:-translate-y-1">
            <div className={`mb-6 flex h-14 w-14 items-center justify-center rounded-2xl border ${colorClasses[color] || colorClasses.indigo}`}>
                {icon}
            </div>
            <p className="text-xs font-bold uppercase tracking-widest text-slate-400 mb-2">{label}</p>
            <div className="flex items-end justify-between">
                <h3 className="text-2xl font-black text-slate-900">{value}</h3>
                {trend && (
                    <div className={`flex items-center gap-1 rounded-full px-2 py-0.5 text-[10px] font-black ${trendUp ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'}`}>
                        {trendUp ? <ArrowUpRight size={10} /> : <ArrowDownRight size={10} />}
                        {trend}
                    </div>
                )}
            </div>
        </div>
    );
};

export default StatCard;
