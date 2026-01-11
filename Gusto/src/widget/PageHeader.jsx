import React from 'react';

const PageHeader = ({ icon: Icon, title, subtitle, actions }) => {
    return (
        <div className="mb-8 flex flex-col justify-between gap-4 md:flex-row md:items-end px-4">
            <div>
                <h1 className="text-4xl font-black tracking-tight text-slate-900 flex items-center gap-3">
                    {Icon && <Icon className="text-indigo-600" size={36} />}
                    {title}
                </h1>
                {subtitle && <p className="mt-1 text-slate-500 font-medium">{subtitle}</p>}
            </div>
            {actions && (
                <div className="flex items-center gap-3">
                    {actions}
                </div>
            )}
        </div>
    );
};

export default PageHeader;
